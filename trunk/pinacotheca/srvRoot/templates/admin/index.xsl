<?xml version="1.0" encoding="utf-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
<head>
	<title>Administration Interface</title>
</head>
<body>
	<h1>Album List</h1>
	<p><a class="addalbum" href="/admin/album/add/">Create new Album</a></p>
	<xsl:for-each select="albumlist/album">
	<div class="album">
		<h2><xsl:value-of select="title"/></h2>
		<p><a class="albumedit"><xsl:attribute name="href">
				/admin/album/edit/<xsl:value-of select="@id"/>
			</xsl:attribute>Edit</a> | 
		<a class="albumdelete"><xsl:attribute name="href">
				/admin/album/delete/<xsl:value-of select="@id"/>
			</xsl:attribute>Delete</a></p>
	</div>
	</xsl:for-each>
</body>
</html>
</xsl:template>
</xsl:transform>