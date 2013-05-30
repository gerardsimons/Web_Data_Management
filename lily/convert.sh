DIR=$(dirname $0)
echo "Parameter 1: $1"
echo "Writing $1 to $1.ly and output/$1.png"
musicxml2ly $DIR/input/$1 -o $DIR/input/$1.ly && lilypond --pdf --output=$DIR/output/$1 $DIR/input/$1.ly
