<!DOCTYPE html>
<html>
<head>
<title>LinkLion - A portal for link discovery.</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="utf-8">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/style.css" rel="stylesheet" media="screen">
<link href="http://fonts.googleapis.com/css?family=Sintony:400,700"
	rel="stylesheet" type="text/css">
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
<link rel="icon" href="favicon.ico" type="image/x-icon"/>
<link rel="shortcut icon" type="image/x-icon" href="favicon.ico"/>
<jsp:useBean id="bean" class="de.linkinglod.beans.HomePage" />
</head>
<body onload="update()">
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation"
		id="header">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand sintony" href="index.jsp"><img
					src="images/linklion-icon.png" border="0"></a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li><a class="sintony align-text" href="index.jsp">LinkLion</a></li>
					<li><a class="sintony align-text" href="start.jsp">Upload</a></li>
					<li><a class="sintony align-text" href="browse.jsp">Browse</a></li>
					<li><a class="sintony align-text" href="vocabulary.html">Vocabulary</a></li>
					<li><a class="sintony align-text" target="_blank"
						href="http://www.linklion.org:8890/sparql">SPARQL</a></li>
					<li><a class="sintony align-text" href="about.html">About</a></li>
				</ul>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>

	<!-- Main jumbotron for a primary marketing message or call to action -->
	<div class="jumbotron">
		<div class="container">
			<div class="centered-text">
				<img src="images/linklion.png" border="0" id="logo">
			</div>
			<h1 class="sintony-bold centered-text">LinkLion</h1>
			<h4 class="sintony centered-text">A portal for link discovery.</h4>
			<div class="sintony centered-text">
				<div id="buttons" class="sintony btn-group">
					<a class="btn btn-primary btn-lg sintony" role="button"
						href="start.jsp"><span class="glyphicon glyphicon-play-circle"></span>&nbsp;&nbsp;Upload</a>
					<a class="btn btn-primary btn-lg sintony" role="button"
						href="browse.jsp"><span class="glyphicon glyphicon-book"></span>&nbsp;&nbsp;Browse</a>
					<a class="btn btn-primary btn-lg sintony" role="button"
						target="_blank" href="http://www.linklion.org:8890/sparql"><span
						class="glyphicon glyphicon-question-sign"></span>&nbsp;&nbsp;SPARQL</a>
				</div>
			</div>
		</div>
	</div>

	<div class="container">
		<!-- Example row of columns -->
		<div class="row">
			<div class="col-md-6">
				<h2 class="sintony centered-text">Why a central link
					repository?</h2>
				<h3 class="sintony">
					<span class="glyphicon glyphicon-hdd"></span>&nbsp;&nbsp;Store
					computed links.
				</h3>
				<h3 class="sintony">
					<span class="glyphicon glyphicon-stats"></span>&nbsp;&nbsp;Compare
					different frameworks.
				</h3>
				<h3 class="sintony">
					<span class="glyphicon glyphicon-link"></span>&nbsp;&nbsp;Maintain
					links when projects shut down.
				</h3>
				<div class="centered-text">
					<div id="readmore" class="sintony btn-group btn-group-sm">
						<a class="btn btn-default btn-lg sintony" role="button"
							href="about.html">Read more</a>
					</div>
				</div>
			</div>
			<div class="col-md-6">
				<h2 class="sintony centered-text">Statistics</h2>
				<table id="dashboard" class="table table-hover table-condensed">
					<tr>
						<th class="sintony">Frameworks</th>
						<td class="sintony" id="fw_count"><img
							src="images/loader.gif" border="0" id="loader"></td>
					<tr>
						<th class="sintony">Mappings</th>
						<td class="sintony" id="m_count"><img src="images/loader.gif"
							border="0" id="loader"></td>
					<tr>
						<th class="sintony">Datasets</th>
						<td class="sintony" id="ds_count"><img
							src="images/loader.gif" border="0" id="loader"></td>
					<tr>
						<th class="sintony">Link types</th>
						<td class="sintony" id="lt_count"><img
							src="images/loader.gif" border="0" id="loader"></td>
					<tr>
						<th class="sintony">Links</th>
						<td class="sintony" id="l_count"><img src="images/loader.gif"
							border="0" id="loader"></td>
					<tr>
						<th class="sintony">Triples</th>
						<td class="sintony" id="t_count"><img src="images/loader.gif"
							border="0" id="loader"></td>
				</table>
			</div>
		</div>
		<hr>

		<footer>
			<p class="sintony">
				Created and maintained by <a href="http://dbs.uni-leipzig.de"
					target="_blank">Database Group Leipzig</a> &amp; <a
					href="http://aksw.org" target="_blank">AKSW</a>, University of
				Leipzig, 2014
			</p>
		</footer>
	</div>
	<!-- /container -->

	<script type="text/javascript">
		function update() {
			Stats.getNumFrameworks(function(data) {
				dwr.util.setValue('fw_count', data);
			});
			Stats.getNumMappings(function(data) {
				dwr.util.setValue('m_count', data);
			});
			Stats.getNumDatasets(function(data) {
				dwr.util.setValue('ds_count', data);
			});
			Stats.getNumLinkTypes(function(data) {
				dwr.util.setValue('lt_count', data);
			});
			Stats.getNumLinks(function(data) {
				dwr.util.setValue('l_count', data);
			});
			Stats.getNumTriples(function(data) {
				dwr.util.setValue('t_count', data);
			});
		}
	</script>

	<!--  DWR for dynamic load of statistics -->
    <script type='text/javascript' src='/portal/dwr/engine.js'></script>
    <script type='text/javascript' src='/portal/dwr/util.js'></script>
    <script type='text/javascript' src='/portal/dwr/interface/Stats.js'></script>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="https://code.jquery.com/jquery.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js"></script>
	<script src="js/script.js"></script>
</body>
</html>
