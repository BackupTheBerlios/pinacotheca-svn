<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>Pinacotheca Startpage</title>
				<link rel="stylesheet" type="text/css" href="/album/stylesheet" />
			</head>
			<body>
				<h1>Pinacotheca Startpage</h1>
				<p>You can view photos <a href="/album/tagfilter">filtered by tags</a> or by choosing an album from the list below.
				</p>
				<xsl:for-each select="albumlist/album">
					<div class="album">
						<h2>
							<xsl:value-of select="name" />
						</h2>
						<p>
							<xsl:value-of select="description" />
						</p>
						<p>
							<a>
								<xsl:attribute name="href">
									/album/show/
									<xsl:value-of select="@id" />
								</xsl:attribute>
								Show Album
							</a>
						</p>
					</div>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:transform>