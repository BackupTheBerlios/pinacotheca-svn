<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/album">
<html>
<head>
<title><xsl:value-of select="title"/></title>
</head>
<body>
	<form method="POST">
	<xsl:attribute name="action">
		/admin/album/edit/<xsl:value-of select="id"/>
	</xsl:attribute>
	<fieldset>
	<legend>Edit Album</legend>
    <p><label for="albumtitle">Album Title:</label><br/>
    <input id="albumtitle" name="albumtitle"><xsl:value-of select="title"/></input></p>
    <p><label for="albumdescription">Album Description:</label><br/>
    <input id="albumdescription" name="albumdescription"><xsl:value-of select="description"/></input></p>
    <button type="submit">Save Changes</button>
    </fieldset>
    </form>
    <form method="POST" enctype="multipart/form-data">
    <xsl:attribute name="action">
    	/admin/album/add/<xsl:value-of select="id"/>
    </xsl:attribute>
    <fieldset>
    <legend>Add Photo to Album</legend>
    <p><label for="photodescription">Description:</label><br/>
    <input id="photodescription" name="photodescription"/></p>
    <p><label for="photofile">File:</label><br/>
    <input type="file" id="photofile" name="photofile" accept="image/jpeg"/></p>
    <button type="submit">Add Photo</button>
    </fieldset>
    </form>
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