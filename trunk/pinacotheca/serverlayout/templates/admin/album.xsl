<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/album">
		<html>
			<head>
				<title>
					Edit Album -
					<xsl:value-of select="name" />
				</title>
				<link rel="stylesheet" type="text/css" href="/admin/stylesheet"></link>
				<script type="text/javascript" src="/album/template/pt.js"></script>
				<script type="text/javascript" src="/admin/template/album.js"></script>
			</head>
			<body>
				<h1>
					Edit Album -
					<xsl:value-of select="name" />
				</h1>
				<p>
					<a href="/admin/">Go back to albumlist</a>
				</p>
				<h2>Photos in this Album</h2>
				<form method="POST">
					<xsl:attribute name="action">
						/admin/tags/assign/
						<xsl:value-of select="id" />
					</xsl:attribute>
					<fieldset>
						<legend>Modify Photos</legend>
						<div class="photolist">
							<xsl:for-each select="photolist/photo">
								<div class="photothumbnail">
									<a>
										<xsl:attribute name="href">
											/admin/photo/edit/
											<xsl:value-of select="@id" />
										</xsl:attribute>
										<img class="photothumbnail">
											<xsl:attribute name="src">
												/album/photo/thumb/
												<xsl:value-of select="@id" />
											</xsl:attribute>
										</img>
									</a>
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
										<a class="photodelete">
											<xsl:attribute name="href">
												/admin/photo/delete/
												<xsl:value-of select="@id" />
											</xsl:attribute>
											Delete
										</a>
									</p>
								</div>
							</xsl:for-each>
						</div>
						<p style="clear:both;padding-top:5px;">
							<select name="tagid" id="tagid"></select>
						</p>
						<button type="submit">Assign to tag</button>
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
						<button type="submit">Save Changes</button>
					</fieldset>
				</form>
				<form method="POST">
					<xsl:attribute name="action">
						/admin/album/key/
						<xsl:value-of select="id" />
					</xsl:attribute>
					<fieldset>
						<legend>Manage Album Key</legend>
						<p>
							<xsl:value-of select="authkey" />
						</p>
						<button type="submit" name="genkey">Generate New Key</button>
						<p />
						<button type="submit" name="delkey">Delete Key</button>
					</fieldset>
				</form>
			</body>
		</html>
	</xsl:template>
</xsl:transform>