<?xml version="1.0" encoding="utf-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/photo">
		<html>
			<head>
				<title>Administration Interface</title>
				<link rel="stylesheet" type="text/css" href="/admin/stylesheet"></link>
				<script type="text/javascript" src="/admin/template/photo.js"></script>
			</head>
			<body>
				<h1>
					Edit Photo:
					<xsl:value-of select="name" />
				</h1>
				<form method="POST">
					<xsl:attribute name="action">
						/admin/photo/edit/
						<xsl:value-of select="id" />
					</xsl:attribute>
					<fieldset>
						<legend>Edit Photo Data</legend>
						<p>
							<label for="photodescription">Photo Description:</label>
							<br />
							<input id="photodescription" name="photodescription">
								<xsl:attribute name="value">
									<xsl:value-of select="description" />
								</xsl:attribute>
							</input>
						</p>
						<p>
							<div style="float:left;">
								<label for="assignedtags">Assigned Tags</label>
								<br />
								<select id="assignedtags" name="assignedtags" multiple="multiple" style="width:100px;" size="5"></select>
							</div>
							<div style="float:left;margin:10px;">
								<br/>
								<button type="button" onclick="ptUnassignSelected();">--&gt;</button><br/>
								<button type="button" onclick="ptAssignSelected();">&lt;--</button>
							</div>
							<div style="float:left;">
								<label for="unassignedtags">Not Assigned Tags</label>
								<br />
								<select id="unassignedtags" name="unassignedtags" multiple="multiple" style="width:100px;" size="5"></select>
							</div>
						</p>
						<p style="clear:left;">
							<button type="submit">Save Changes</button>
						</p>
					</fieldset>
				</form>
				<p>
					<img class="photodisplay">
						<xsl:attribute name="src">
							/album/photo/
							<xsl:value-of select="id" />
						</xsl:attribute>
					</img>
				</p>
			</body>
		</html>
	</xsl:template>
</xsl:transform>