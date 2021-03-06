package de.linkinglod.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;

import de.linkinglod.db.User;
import de.linkinglod.io.Reader;
import de.linkinglod.rdf.RDFMappingProcessor;
import de.linkinglod.rdf.TripleStoreWriter;
import de.linkinglod.util.MD5Utils;
 
/**
 * @author Markus Nentwig <nentwig@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
@Path("/file")
public class UploadFileService implements Reader {
	
	private static Logger log = LoggerFactory.getLogger(UploadFileService.class);

	/**
	 * Upload MULTIPART_FORM_DATA file, save to Java temp directory.
	 * @param stream
	 * @param fileDetail
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(FormDataMultiPart form) throws IOException, URISyntaxException {
		log.debug("File upload service triggered!");
		
		// 1. process meta data
		// TODO delete file if overall upload was not successful!
		String filePathAndName = writeSourceFileToDisk(form);
		// TODO meta data missing?
		MD5Utils.reset();
		String mappingHash = MD5Utils.computeChecksum(filePathAndName);
		String fileName = getFileName(filePathAndName);
    	// TODO  note: Hibernate User() should not be used here, it is only for creating (hibernate) database User() objects! 
    	// REWORK this if users are implemented!
    	User demoUser = new User();
    	demoUser.setName("Demo User");
		// TODO User is NOT used in triple store in any way.
    	// TODO fix date issue
		
		RDFMappingProcessor processor = new RDFMappingProcessor(mappingHash, fileName, demoUser, new Date());
    	processor = addFormDataToProcessor(form, processor);
		
    	// 2. process data
		// splitSourceIfNeeded
		
		// process with transform for each chunk
		
		Model model = read(filePathAndName);
    	System.out.println("model.isEmpty(): " + model.isEmpty());
    	
    	TripleStoreWriter tsw = new TripleStoreWriter();
//    	DBCommunication dbComm = new DBCommunication();

    	Model modelOut = processor.transform(model);
    	
    	tsw.write(LLProp.getString("TripleStore.graph"), modelOut);
    	tsw.write(LLProp.getString("TripleStore.graph"), OntologyLoader.getOntModel());
    	
//    	dbComm.createUser(demoUser);
//    	
//    	dbComm.write("TripleStore.graph", modelOut);
//    	dbComm.write("TripleStore.graph", OntologyLoader.getOntModel());
    	
    	// TODO check later, IF this is needed. Source file + speed is more important in the beginning.
		//String sourceFileName = form.getField("file").getContentDisposition().getFileName();
 		//writeModifiedDataToFile(sourceFileName);
		
		// FIXME Adding this throws an exception, but on line 162. Investigate why.
//		File f = new File(fileOutLocation);
//		String fileName = new SimpleDateFormat("yyyy-MM-dd_hhmmss").format(new Date()) + ".ttl";
//		f.renameTo(new File(System.getProperty("catalina.base") + "/webapps/LinkingLOD-0.0.1-SNAPSHOT/" + fileName));
//		String output = "<a href='" + fileName + "'>Download turtle file</a>";
			
		// TODO change this hack with something else
		return Response.status(200).entity("<html><head><meta http-equiv='refresh' content='0;url=../../success.html'></head></html>").build();
	}
	
	private RDFMappingProcessor addFormDataToProcessor(FormDataMultiPart form,
			RDFMappingProcessor processor) {
        Map<String, List<FormDataBodyPart>> formParts = form.getFields();
        
        String existingSrcDsURI = formParts.get("existing-source-uri").get(0).getValue();
        if(existingSrcDsURI.equals(""))
	    	processor.addNewDataset(formParts.get("new-source-name").get(0).getValue(), 
	    			form.getField("new-source-urispace").getValue(),
	    			"source");
        else
        	processor.setSourceDataset(existingSrcDsURI);
        
        String existingTgtDsURI = formParts.get("existing-target-uri").get(0).getValue();
        if(existingTgtDsURI.equals(""))
	    	processor.addNewDataset(formParts.get("new-target-name").get(0).getValue(), 
	    			form.getField("new-target-urispace").getValue(), 
	    			"target");
        else
        	processor.setTargetDataset(existingTgtDsURI);
    	
    	String existingFwURI = formParts.get("existing-framework-uri").get(0).getValue();
    	if(existingFwURI.equals(""))
    		processor.addNewFramework(formParts.get("new-framework-name").get(0).getValue(),
    				formParts.get("new-framework-version").get(0).getValue(),
    				formParts.get("new-framework-url").get(0).getValue()
    				);
    	else
    		processor.setFramework(existingFwURI);
    	
    	String existingAlgoURI = "";
    	String newAlgoName = "";
    	String newAlgoUrl = "";
    	if (formParts.containsKey("existing-algorithm-uri")) {
    		existingAlgoURI = formParts.get("existing-algorithm-uri").get(0).getValue();
    	}
    	if (existingAlgoURI.equals("")
    			&& formParts.containsKey("new-algorithm-name")
    			&& formParts.containsKey("new-algorithm-url")) {
    		newAlgoName = formParts.get("new-algorithm-name").get(0).getValue();
    		newAlgoUrl = formParts.get("new-algorithm-url").get(0).getValue();
    		
	    	processor.addNewAlgorithm(newAlgoName, newAlgoUrl);
    	}
    	else
    		processor.setAlgorithm(existingAlgoURI);

        return processor;
	}

	/**
	 * Read file and create Jena model.
	 * @param stream
	 * @param fileDetail
	 * @return Jena model
	 * @throws NoSuchAlgorithmException 
	 * @throws FileNotFoundException 
	 */
	@POST
	@Path("/read")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response readFile(@FormDataParam("file") InputStream stream,
							 @FormDataParam("file") FormDataContentDisposition fileDetail) 
									   throws NoSuchAlgorithmException, FileNotFoundException {
				
		log.debug("File read service triggered!");
		 
		return Response.status(200).entity("File read service triggered").build();
	}
 
	/**
	 * Save uploaded file to specified location.
	 * @param form
	 * @return 
	 * @throws IOException
	 */
	private String writeSourceFileToDisk(FormDataMultiPart form) throws IOException {
		FormDataBodyPart formFile = form.getField("file");
        String formName =  formFile.getContentDisposition().getFileName();
        
        String pathToTest = suggestName(formName);
		File file = new File(pathToTest);
		while (file.isFile()) {
	        String lastExistingFile = pathToTest;
			pathToTest = getNewNameIfHashDiffers(formFile, pathToTest);
			if (pathToTest == null) { // file exists, same content
				return lastExistingFile;
			}
			file = new File(pathToTest);
		}
		
        InputStream readFormStream = formFile.getValueAs(InputStream.class);
		writeFile(readFormStream, file);
		
		return pathToTest;
	}
	
	private String getFileName(String fullPath) {
		if (fullPath.contains("/")) {
			String[] parts = fullPath.split("/");
			return parts[parts.length - 1];
		} 
		else {
		    throw new IllegalArgumentException("String " + fullPath + " does not contain '/'");
		}
	}

	/**
	 * Checks if the existing file has the same hash as the file from the form and renames if needed. If hash equals, existing name is used.
	 * @param formFile
	 * @param pathToTest
	 * @return 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private String getNewNameIfHashDiffers(FormDataBodyPart formFile,
			String pathToTest) throws IOException, FileNotFoundException {
		InputStream hashFormStream = formFile.getValueAs(InputStream.class);

		String formHash = DigestUtils.md5Hex(hashFormStream);
		String diskHash = MD5Utils.computeChecksum(pathToTest);
		
		if (!formHash.equals(diskHash)) { // hash is different -> new name
			String nextTry = "";
			if (pathToTest.endsWith(".nt")) {
				nextTry = pathToTest.substring(0, pathToTest.length() - 3);
				nextTry = setNextName(nextTry);
				nextTry += ".nt";
			}
			else {
				nextTry = setNextName(nextTry);
			}
			
			return nextTry;
		}

		return null;
	}

	/**
	 * Set next name if file hashes are equal.
	 * @param nextTry 
	 * @return
	 */
	private String setNextName(String nextTry) {
		if (nextTry.matches(".*\\(\\d*\\)")) { // regex, i.e., abc(12)
			// number -> number + 1
			int extractDigitAndInc = Integer.parseInt(nextTry.replaceAll(".*\\((\\d*)\\)", "$1")) + 1;
			// reconstruct string
			return nextTry.replaceAll("(.*\\()\\d*(\\))", "$1" + String.valueOf(extractDigitAndInc) + "$2");
		}
		else {
			return nextTry += "(1)";
		}
	}

	/**
	 * Suggest next name for the file name.
	 * TODO change directory for download, if needed. dont put that path in here.
	 * @param fName
	 * @return
	 */
	private String suggestName(String fName) {
		String downloadDir = "/u/wditomcat/htdocs/download/mapping";
		
		String path = downloadDir + System.getProperty("file.separator");
		
		return path + fName;
	}

	/**
	 * @param readFormStream stream object where to read from
	 * @param file file to write to
	 * @throws FileNotFoundException
	 * @throws IOException
	 * TODO do a simple check if file is rdf
	 */
	@SuppressWarnings("resource")
	private void writeFile(InputStream readFormStream, File file)
			throws FileNotFoundException, IOException {
		OutputStream out = new FileOutputStream(file);
		
		int read = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(file);
		while ((read = readFormStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
		System.out.println("Write file to: " + file.getPath());

	}

	@Override
	public Model read(String pathToFile) throws FileNotFoundException {
		InputStream stream = new FileInputStream(pathToFile);
		
		CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.IGNORE);
		InputStreamReader reader = new InputStreamReader(stream, decoder);
		
		Model model = ModelFactory.createDefaultModel();
		// TODO support different InputFormats like "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3".
		// Malformed statements have to be corrected/deleted manually, 
		// create error page for this:
		// org.apache.jena.riot.RiotException: [line: 1, col: 94] Unknown char: /(47;0x002F)
		// (example statement for this sort of error (missing <> in object): 
		// <http://create.canterbury.ac.uk/id/subject/DJK> 
		//   <http://www.w3.org/2002/07/owl#sameAs> 
		//   https://eprints.soas.ac.uk/id/subject/DJK .
		// )
		model.read(reader, null, LLProp.getString("tripleInputFormat"));
		log.debug("Read " + model.size() + " elements.");

		return model;
	}
}