echo "Clearing musicXMLs and output files..."
BASEDIR=$(dirname $0)
rm -rf $BASEDIR/output/*.png
rm -rf $BASEDIR/output/*.pdf
rm -rf $BASEDIR/input/*.xml
rm -rf $BASEDIR/input/*.ly

