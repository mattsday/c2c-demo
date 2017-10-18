#!/bin/sh

if ! command -v mvn >/dev/null 2>&1; then
	echo Plesae install Maven
	echo e.g. brew install mvn
	exit
fi

cd backend && mvn -DskipTests package && cd ../backend && mvn -DskipTests package && cd ..

if [ $? -gt 0 ]; then
	echo "Error(s) occured building"
	exit
else
	echo 'Done!'
fi
