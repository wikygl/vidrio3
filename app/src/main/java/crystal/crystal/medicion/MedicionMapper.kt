package crystal.crystal.medicion

fun ItemMedicionObraEntity.toDomain(): ItemMedicionObra {
    return ItemMedicionObra(
        id = id,
        numero = numero,
        clienteId = clienteId,
        clienteNombre = clienteNombre,
        categoria = categoria,
        sistema = sistema,
        tipoApertura = tipoApertura,
        geometria = geometria,
        encuentros = encuentros,
        modelo = modelo,
        acabado = acabado,
        especificaciones = especificaciones,
        accesorios = accesorios,
        anchoCm = anchoCm,
        altoCm = altoCm,
        alturaPuenteCm = alturaPuenteCm,
        unidadCaptura = runCatching { UnidadMedida.valueOf(unidadCaptura) }.getOrDefault(UnidadMedida.CM),
        ubicacionObra = ubicacionObra,
        notasObra = notasObra,
        evidencias = evidencias,
        estado = runCatching { EstadoMedicion.valueOf(estado) }.getOrDefault(EstadoMedicion.BORRADOR),
        pendienteSincronizar = pendienteSincronizar,
        medidoPor = medidoPor,
        dispositivoId = dispositivoId,
        creadoEn = creadoEn,
        actualizadoEn = actualizadoEn
    )
}

fun ItemMedicionObra.toEntity(): ItemMedicionObraEntity {
    return ItemMedicionObraEntity(
        id = id,
        numero = numero,
        clienteId = clienteId,
        clienteNombre = clienteNombre,
        categoria = categoria,
        sistema = sistema,
        tipoApertura = tipoApertura,
        geometria = geometria,
        encuentros = encuentros,
        modelo = modelo,
        acabado = acabado,
        especificaciones = especificaciones,
        accesorios = accesorios,
        anchoCm = anchoCm,
        altoCm = altoCm,
        alturaPuenteCm = alturaPuenteCm,
        unidadCaptura = unidadCaptura.name,
        ubicacionObra = ubicacionObra,
        notasObra = notasObra,
        evidencias = evidencias,
        estado = estado.name,
        pendienteSincronizar = pendienteSincronizar,
        medidoPor = medidoPor,
        dispositivoId = dispositivoId,
        creadoEn = creadoEn,
        actualizadoEn = actualizadoEn
    )
}

