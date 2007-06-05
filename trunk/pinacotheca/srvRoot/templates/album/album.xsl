<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/album">
<html>
<head>
<title><xsl:value-of select="title"/></title>
</head>
<body>
    <h1><xsl:value-of select="title"/></h1>
    <p><xsl:value-of select="description"/></p>
    <xsl:for-each select="photolist/photo">
    	<div class="photo">
    	<div class="photothumbnail">
    	<img>
    	<xsl:attribute name="src">
    		/album/photo/thumb/<xsl:value-of select="@id"/>
    	</xsl:attribute>
    	</img>
    	<p class="phototitle"><xsl:value-of select="@title"/></p>
    	</div>
    	</div>
    </xsl:for-each>
</body>
</html>
</xsl:template>
</xsl:transform>