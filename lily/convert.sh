echo "Converting musicXML..."
musicxml2ly musicXML.xml -o output/music.ly && lilypond --png --output=output/music output/music.ly 

