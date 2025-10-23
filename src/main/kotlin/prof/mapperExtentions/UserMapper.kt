package prof.mapperExtentions

import prof.entities.UserDTO
import prof.responses.GetUserResponse
import prof.responses.GetUsersResponse

fun UserDTO.toGetUserResponse(): GetUserResponse = GetUserResponse(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun List<UserDTO>.toGetUserResponseList(): List<GetUserResponse> =
    map { it.toGetUserResponse() }

fun List<UserDTO>.toGetUsersResponse(): GetUsersResponse =
    GetUsersResponse(GetUsersResponseList = map { it.toGetUserResponse() }.toMutableList())
