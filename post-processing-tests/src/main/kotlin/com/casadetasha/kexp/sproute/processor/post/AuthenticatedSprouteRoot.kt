package com.casadetasha.kexp.sproute.processor.post

import com.casadetasha.kexp.sproute.annotations.Authenticated
import com.casadetasha.kexp.sproute.annotations.Sproute

@Sproute("/authenticated_sproute_root")
@Authenticated
interface AuthenticatedSprouteRoot
