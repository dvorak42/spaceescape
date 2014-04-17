#!/usr/bin/python
#ls ../art | grep png | sed "s/.png//g" | python generateInitial.py
import sys

template = """{"name":"FILE","imagePath":"../art/FILE.png","origin":{"x":0,"y":0},"polygons":[],"circles":[],"shapes":[]},"""

data = '{"rigidBodies":['

for line in sys.stdin:
    f = line.strip()
    data += template.replace('FILE', f)

data = data[:-1]
data += '],"dynamicObjects":[]}'

f = open('main_bodies.json', 'w')
f.write(data)
f.close()


