#!/usr/bin/env bash
set -e

# Read environment variables from file and export them.
file_env() {
	while read -r line || [[ -n $line ]]; do
		echo $line
		export $line
	done < "$1"
}

# if file exists then export enviroment variables
for FILE in $MOUNT_DIRECTORY/*.env; do
	file_env $FILE
done

exec "$@"
