package com.shubham.neocal.utils

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "queryresult", strict = false)
data class WolframResponse(
    @param:Attribute(name = "success")
    @get:Attribute(name = "success")
    val success: Boolean,

    @param:ElementList(inline = true, required = false)
    @get:ElementList(inline = true, required = false)
    val pods: List<Pod>? = emptyList()
)

@Root(name = "pod", strict = false)
data class Pod(
    @param:Attribute(name = "title")
    @get:Attribute(name = "title")
    val title: String,

    @param:Attribute(name = "primary", required = false)
    @get:Attribute(name = "primary", required = false)
    val primary: Boolean? = false,

    @param:Element(name = "states", required = false)
    @get:Element(name = "states", required = false)
    val stateInfo: StateInfo? = null,

    @param:ElementList(inline = true, required = false)
    @get:ElementList(inline = true, required = false)
    val subpods: List<SubPod>? = emptyList()
) {
    val states: List<State>
        get() = stateInfo?.states ?: emptyList()
}

@Root(name = "subpod", strict = false)
data class SubPod(
    @param:Element(name = "plaintext", required = false)
    @get:Element(name = "plaintext", required = false)
    val plaintext: String? = ""
)

@Root(name = "states", strict = false)
data class StateInfo(
    @param:ElementList(inline = true, required = false)
    @get:ElementList(inline = true, required = false)
    val states: List<State>? = emptyList()
)

@Root(name = "state", strict = false)
data class State(
    @param:Attribute(name = "name")
    @get:Attribute(name = "name")
    val name: String,

    @param:Attribute(name = "input")
    @get:Attribute(name = "input")
    val input: String
)