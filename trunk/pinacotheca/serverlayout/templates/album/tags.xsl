<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>Tag Filtering View</title>
				<link rel="stylesheet" type="text/css" href="/album/stylesheet"></link>
				<script type="text/javascript" src="/album/template/pt.js"></script>
				<script type="text/javascript" src="/album/template/tags.js"></script>
			</head>
			<body>
				<h1>Viewing Photos by Tags</h1>
				<p>
					<a href="/album/">Go back to albumlist</a>
				</p>
				<h2>Photos</h2>
				<p class="fullwidth">
					Choose a Filter from the selection which is labeled
					<strong>Available Filters</strong>
					, click the add button and then the Photolist will load all photos which are tagged with the chosen tag. If you add more
					than one tag, then every photo that has
					<em>any</em>
					of the selected tags is displayed.
				</p>
					<fieldset id="tagfilter">
						<legend>Filtering Options</legend>
						<p>
							<label for="unapplied">Available Filters</label>
							<br />
							<select size="1" id="unapplied">
								<xsl:for-each select="taglist/tag">
									<option>
										<xsl:attribute name="value">
											<xsl:value-of select="@id" />
										</xsl:attribute>
										<xsl:value-of select="@name" />
									</option>
								</xsl:for-each>
							</select>
							<br />
						</p>
						<button type="button" onclick="javascript:addToFilter();" class="filterbutton">Add</button>
						<p>
							<label for="applied">Applied Filters</label>
							<br />
							<select size="1" id="applied"></select>
							<br />
						</p>
						<button type="button" onclick="javascript:removeFromFilter();" class="filterbutton">Remove</button>
					</fieldset>
					<div id="tagphotolist"></div>
			</body>
		</html>
	</xsl:template>
</xsl:transform>