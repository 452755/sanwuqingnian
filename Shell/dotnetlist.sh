CRTDIR=$(pwd)

BASENAME=${PWD##*/}

#echo ${CRTDIR}
#echo ${BASENAME}

CSPROJ=".""/""${BASENAME}"".csproj"

#echo ${CSPROJ}

dotnet list "${CSPROJ}" package

dotnet list "${CSPROJ}" reference
