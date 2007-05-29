<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
<head>
	<title>Albumlist</title>
</head>
<body>
	<h1>Albumlist</h1>
	<xsl:for-each select="albumlist/album">
	<div class="album">
		<h2><a>
			<xsl:attribute name="href">
				/album/show/<xsl:value-of select="@id"/>/
			</xsl:attribute>
			<xsl:value-of select="title"/>
			</a></h2>
		<p><xsl:value-of select="description"/></p>
	</div>
	</xsl:for-each>
</body>
</html>
</xsl:template>
</xsl:transform>