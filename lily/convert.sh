musicxml2ly $1 -o output/$1.ly && lilypond --png --output=output/$1 output/$1.ly
echo output/$i.png
