/*
 *   Copyright 2009 The Portico Project
 *
 *   This file is part of pgauge.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL)
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
#ifndef __COMMON_H_
#define __COMMON_H_

#pragma once

#ifdef WIN32
    #define WIN32_LEAN_AND_MEAN
    #include <windows.h>
#elif defined(__APPLE__)
    #include <malloc/malloc.h>
    #include <stdarg.h>
    #include <ctype.h>
    #include <ext/hash_map>
	#include <sys/time.h>
#else
    #include <malloc.h>
    #include <stdarg.h>
    #include <ctype.h>
	#include <ext/hash_map>
	#include <string>
	#include <cstring>
	#include <sys/time.h>
#endif

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <iostream>
#include <memory>
#include <fstream>
#include <sstream>
#include <set>
#include <map>
#include <vector>
#include <typeinfo>
#include <algorithm>

/** bring in the RTI headers */
#include "RTI.hh"
#include "fedtime.hh"

#define PGAUGE_NAMESPACE namespace pgauge {
#define PGAUGE_NAMESPACE_END };

using namespace std;

#endif
