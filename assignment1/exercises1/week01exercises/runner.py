#!/usr/bin/env python3
import os
import sys

if len(sys.argv) < 3:
    print("./runner.py <exercises> <filename>")
    exit(-1)

exercises = sys.argv[1]
filename = sys.argv[2]

os.system(f"gradle -PmainClass={exercises}.{filename} --console=plain run")