<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/album">
		<html>
			<head>
				<title>
					Edit Album:
					<xsl:value-of select="name" />
				</title>
				<link rel="stylesheet" type="text/css" href="/admin/stylesheet"></link>
				<script type="text/javascript" src="/admin/template/album.js"></script>
			</head>
			<body>
				<h1>
					Edit Album:
					<xsl:value-of select="name" />
				</h1>
				<h2>Photos in this Album</h2>
				<form method="POST">
					<xsl:attribute name="action">
						/admin/tags/assign/
						<xsl:value-of select="id" />
					</xsl:attribute>
					<fieldset>
						<xsl:for-each select="photolist/photo">
							<div class="photo">
								<div class="photothumbnail">
									<img class="photothumbnail">
										<xsl:attribute name="src">
											/album/photo/
											<xsl:value-of select="@id" />
										</xsl:attribute>
									</img>
									<p>
										<input type="checkbox">
											<xsl:attribute name="name">
												assigntag
												<xsl:value-of select="@id" />
											</xsl:attribute>
											<xsl:attribute name="id">
												assigntag
												<xsl:value-of select="@id" />
											</xsl:attribute>
										</input>
										<label>
											<xsl:attribute name="for">
												assigntag
												<xsl:value-of select="@id" />
											</xsl:attribute>
											assign to Tag
										</label>
									</p>
									<p class="phototitle">
										<xsl:value-of select="@title" />
									</p>
									<p>
										<a class="photoedit">
											<xsl:attribute name="href">
												/admin/photo/edit/
												<xsl:value-of select="@id" />
											</xsl:attribute>
											Edit
										</a>
										|
										<a class="photodelete">
											<xsl:attribute name="href">
												/admin/photo/delete/
												<xsl:value-of select="@id" />
											</xsl:attribute>
											Delete
										</a>
									</p>
								</div>
							</div>
						</xsl:for-each>
						<p style="clear:left;">
							<select name="tagid" id="tagid"></select>
						</p>
						<p>
							<button type="submit">Assign to Tag</button>
						</p>
					</fieldset>
				</form>
				<form method="POST" enctype="multipart/form-data">
					<xsl:attribute name="action">
						/admin/photo/add/
						<xsl:value-of select="id" />
					</xsl:attribute>
					<fieldset>
						<legend>Add Photo to Album</legend>
						<p>
							<label for="photodescription">Description:</label>
							<br />
							<input id="photodescription" name="photodescription" />
						</p>
						<p>
							<label for="photofile">File:</label>
							<br />
							<input type="file" id="photofile" name="photofile" accept="image/jpeg" />
						</p>
						<button type="submit">Add Photo</button>
					</fieldset>
				</form>
				<h2 style="clear:left">Album Information</h2>
				<form method="POST">
					<xsl:attribute name="action">
						/admin/album/edit/
						<xsl:value-of select="id" />
					</xsl:attribute>
					<fieldset>
						<legend>Edit Album</legend>
						<p>
							<label for="albumname">Album Title:</label>
							<br />
							<input id="albumname" name="albumname">
								<xsl:attribute name="value">
									<xsl:value-of select="name" />
								</xsl:attribute>
							</input>
						</p>
						<p>
							<label for="albumdescription">Album Description:</label>
							<br />
							<textarea rows="5" cols="35" id="albumdescription" name="albumdescription">
								<xsl:value-of select="description" />
							</textarea>
						</p>
						<p>
							<button type="submit">Save Changes</button>
						</p>
					</fieldset>
				</form>
			</body>
		</html>
	</xsl:template>
</xsl:transform>