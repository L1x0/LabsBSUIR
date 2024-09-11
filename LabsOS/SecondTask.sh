outout="$1"
dir="$2"
exst="$3"

if [ ! -d "$dir" ]; 
then
echo "dir error"
exit 1
fi

find "$dir" -maxdepth 1 -type f -name "*.$exst" -exec basename {} \; > /home/parallels/Desktop/LabsOS/output  