#!/usr/bin/python
import json
import sys
import time

releasesFile = open("releases.json", "r")
releases = json.loads(releasesFile.read())
releasesFile.close()

if len(sys.argv) < 3:
    print("Syntax: {} <release> <Changes>".format(sys.argv[0]))
    exit()

release = {
    "version": sys.argv[1],
    "release": int(time.time() * 1000),
    "changes": " ".join(sys.argv[2:len(sys.argv)])
}

releases.append(release)

releasesFile = open("releases.json", "w")
releasesFile.write(json.dumps(releases, indent=2))
releasesFile.close()
