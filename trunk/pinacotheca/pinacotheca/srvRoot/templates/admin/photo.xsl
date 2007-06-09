<?xml version="1.0" encoding="utf-8"?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/photo">
<html>
<head>
	<title>Administration Interface</title>
	<link rel="stylesheet" type="text/css" href="/admin/stylesheet"/>
</head>
<body>
	<h1>Edit Photo: <xsl:value-of select="name"/></h1>
	<form method="POST">
		<xsl:attribute name="action">
		/admin/photo/edit/<xsl:value-of select="id"/>
		</xsl:attribute>
	<fieldset>
	<legend>Edit Photo Data</legend>
    <p><label for="photodescription">Photo Description:</label><br/>
    <input id="photodescription" name="photodescription"><xsl:attribute name="value">
    	<xsl:value-of select="description"/>
    	</xsl:attribute></input></p>
    <p><button type="submit">Save Changes</button></p>
    </fieldset>
    </form>
    <p>
   	<img class="photodisplay">
    	<xsl:attribute name="src">
    		/album/photo/<xsl:value-of select="id"/>
    	</xsl:attribute>
   	</img>
   	</p>
</body>
</html>
</xsl:template>
</xsl:transform>