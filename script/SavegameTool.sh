#!/bin/sh

echo ""
echo ""

if [ ! -f SavegameTool.jar ]; then
  echo "SavegameTool.jar not found."
else
  java -version &>/dev/null

  if [ "${?}" -ne 0 ]; then
    echo "Java seems to be not properly installed. The JAVA_HOME/java environment variable might not be setup correctly."
  else
    java -jar SavegameTool.jar "$@"
  fi
fi

echo ""