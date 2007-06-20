<?xml version="1.0" encoding="utf-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>Pinacotheca Administration</title>
				<link rel="stylesheet" type="text/css" href="/admin/stylesheet"></link>
			</head>
			<body>
				<h1>Albumlist</h1>
				<xsl:for-each select="albumlist/album">
					<div class="album">
						<h2>
							<xsl:value-of select="name" />
						</h2>
						<p>
							<xsl:value-of select="description" />
						</p>
						<p>
							<a class="albumedit">
								<xsl:attribute name="href">
									/admin/album/edit/
									<xsl:value-of select="@id" />
								</xsl:attribute>
								<xsl:text>Edit</xsl:text>
							</a>
							|
							<a class="albumdelete">
								<xsl:attribute name="href">
									/admin/album/delete/
									<xsl:value-of select="@id" />
								</xsl:attribute>
								Delete
							</a>
						</p>
					</div>
				</xsl:for-each>
				<h1>Actions</h1>
				<p>
					<a class="tags" href="/admin/tags/">Edit Tags</a>
				</p>
				<form action="/admin/album/add" method="POST">
					<fieldset>
						<legend>Add an Album</legend>
						<p>
							<label for="albumname">Name:</label>
							<br />
							<input type="text" id="albumname" name="albumname" />
						</p>
						<p>
							<label for="albumdescription">Description</label>
							<br />
							<textarea rows="5" cols="35" id="albumdescription" name="albumdescription"></textarea>
						</p>
						<p>
							<button type="submit">Add</button>
						</p>
					</fieldset>
				</form>
			</body>
		</html>
	</xsl:template>
</xsl:transform>