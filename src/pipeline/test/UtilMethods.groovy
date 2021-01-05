package pipeline.test

def getValidatedStages(String chosenStages, ArrayList pipelineStages) {
    def stages = []

    if (chosenStages?.trim()) {
        chosenStages.split(';').each{
            if (it in pipelineStages){
                stages.add(it)
            } else {
                env.FAIL_MESSAGE = "No existe el stage ${it}, por lo que no se pudo realizar la ejecución"
                error "${it} no existe como Stage. Stages disponibles para ejecutar: ${pipelineStages}"
            }
        }
        println "Validación de stages correcta. Se ejecutarán los siguientes stages en orden: ${stages}"
    } else {
        stages = pipelineStages
        println "Parámetro de stages vacío. Se ejecutarán todos los stages en el siguiente orden: ${stages}"
    }

    return stages   
}

return this