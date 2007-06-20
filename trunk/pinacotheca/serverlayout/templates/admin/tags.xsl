<?xml version="1.0" encoding="utf-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>Administration Interface</title>
				<link rel="stylesheet" type="text/css" href="/admin/stylesheet"></link>
			</head>
			<body>
				<h1>Edit Tags</h1>
				<p>
					<a href="/admin/">Go back to albumlist</a>
				</p>
				<h2>Taglist</h2>
				<xsl:for-each select="taglist/tag">
					<form method="POST">
						<xsl:attribute name="action">
							/admin/tags/edit/
							<xsl:value-of select="@id" />
						</xsl:attribute>
						<fieldset>
							<legend>Edit Tag</legend>
							<p>
								<label for="tagname">Tag Name (leave blank to delete):</label>
								<br />
								<input id="tagname" name="tagname">
									<xsl:attribute name="value">
										<xsl:value-of select="@name" />
									</xsl:attribute>
								</input>
							</p>
							<button type="submit">Save changes</button>
						</fieldset>
					</form>
				</xsl:for-each>
				<h2>Actions</h2>
				<form method="POST" action="/admin/tags/add">
					<fieldset>
						<legend>Add Tag</legend>
						<p>
							<label for="tagname">Tag Name:</label>
							<br />
							<input id="tagname" name="tagname" />
						</p>
						<p>
							<button type="submit">Add Tag</button>
						</p>
					</fieldset>
				</form>
			</body>
		</html>
	</xsl:template>
</xsl:transform>