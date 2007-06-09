<?xml version="1.0" encoding="utf-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
<head>
	<title>Administration Interface</title>
</head>
<body>
	<h1>Tag List</h1>
	<form method="POST" action="/admin/tags/add">
	<fieldset>
	<legend>Add Tag</legend>
    <p><label for="tagname">Tag Name:</label><br/>
    <input id="tagname" name="tagname"/></p>
    <p><button type="submit">Add Tag</button></p>
    </fieldset>
    </form>
	<xsl:for-each select="taglist/tag">
	<form method="POST">
	<xsl:attribute name="action">
		/admin/tags/edit/<xsl:value-of select="@id"/>
	</xsl:attribute>
	<fieldset>
	<legend>Edit Tag</legend>
    <p><label for="tagname">Tag Name:</label><br/>
    <input id="tagname" name="tagname"><xsl:attribute name="value">
	    <xsl:value-of select="@name"/>
    </xsl:attribute></input></p>
    <p><button type="submit">Edit Tag</button> | <a><xsl:attribute name="href">
    	/admin/tags/delete/<xsl:value-of select="@id"/>
    	</xsl:attribute>Delete</a></p>
   	</fieldset>
   	</form>
	</xsl:for-each>
</body>
</html>
</xsl:template>
</xsl:transform>