package com.github.irvifa.meetups.cloud.api

import com.fasterxml.jackson.annotation.JsonAutoDetect

class ApiResponseV1<T>(val data: T)

// hack so that empty data can be returned without error
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
class EmptyResponse { // we don't have anything to return
}
