package prof.mapperExtentions

import prof.entities.TermDTO
import prof.responses.GetTermResponse
import prof.responses.GetTermsResponse

fun TermDTO.toGetTermResponse(): GetTermResponse = GetTermResponse(
    id = id,
    title = title,
    content = content,
    version = version,
    active = active,
    userId = userId,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun List<TermDTO>.toGetTermResponseList(): List<GetTermResponse> =
    map { it.toGetTermResponse() }

fun List<TermDTO>.toGetTermsResponse(): GetTermsResponse =
    GetTermsResponse(GetTermsResponseList = map { it.toGetTermResponse() }.toMutableList())
