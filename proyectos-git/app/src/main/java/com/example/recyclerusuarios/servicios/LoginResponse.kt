package com.example.recyclerusuarios.servicios

class LoginResponse {
    private var operario: Operario? = null
    private var respuesta: Respuesta? = null

    fun getOperario(): Operario? {
        return operario
    }

    fun setOperario(operario: Operario?) {
        this.operario = operario
    }

    fun getRespuesta(): Respuesta? {
        return respuesta
    }

    fun setRespuesta(respuesta: Respuesta?) {
        this.respuesta = respuesta
    }

    override fun toString(): String {
        return "LoginResponse(operario=$operario, respuesta=$respuesta)"
    }


}