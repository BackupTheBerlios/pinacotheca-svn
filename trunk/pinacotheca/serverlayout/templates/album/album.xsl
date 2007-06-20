<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/album">
		<html>
			<head>
				<title>
					Viewing Album -
					<xsl:value-of select="name" />
				</title>
				<link rel="stylesheet" type="text/css" href="/album/stylesheet" />
			</head>
			<body>
				<h1>
					Viewing Album -
					<xsl:value-of select="name" />
				</h1>
				<p>
					<a href="/album/">Go back to albumlist</a>
				</p>
				<h2>Description</h2>
				<p>
					<xsl:value-of select="description" />
				</p>
				<h2>Photos in this Album</h2>
				<p>
					<a>
						<xsl:attribute name="href">
							/album/diashow/
							<xsl:value-of select="photolist/photo/@id" />
						</xsl:attribute>
						View all as diashow
					</a>
				</p>
				<div class="photolist">
					<xsl:for-each select="photolist/photo">
						<div class="photothumbnail">
							<a>
								<xsl:attribute name="href">
									/album/show/photo/
									<xsl:value-of select="@id" />
								</xsl:attribute>
								<img class="photothumbnail">
									<xsl:attribute name="src">
										/album/photo/thumb/
										<xsl:value-of select="@id" />
									</xsl:attribute>
									<xsl:attribute name="alt">
										<xsl:value-of select="@name" />
									</xsl:attribute>
								</img>
							</a>
							<p class="phototitle">
								<xsl:value-of select="@name" />
							</p>
						</div>
					</xsl:for-each>
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:transform>