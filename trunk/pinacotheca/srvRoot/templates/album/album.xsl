<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/album">
<html>
<head>
	<title><xsl:value-of select="title"/></title>
	<link rel="stylesheet" type="text/css" href="/album/stylesheet"/>
</head>
<body>
    <h1><xsl:value-of select="name"/></h1>
    <p><xsl:value-of select="description"/></p>
    <xsl:for-each select="photolist/photo">
    	<div class="photo">
    	<div class="photothumbnail">
    	<a><xsl:attribute name="href">
    		/album/show/photo/<xsl:value-of select="@id"/>
    	</xsl:attribute>
    	<img class="photothumbnail">
    	<xsl:attribute name="src">
    		/album/photo/<xsl:value-of select="@id"/>
    	</xsl:attribute>
    	<xsl:attribute name="alt">
    		<xsl:value-of select="@name"/>
    	</xsl:attribute>
    	</img></a>
    	<p class="phototitle"><xsl:value-of select="@name"/></p>
    	</div>
    	</div>
    </xsl:for-each>
</body>
</html>
</xsl:template>
</xsl:transform>