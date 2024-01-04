package be.technobel.playzone.bll.exceptions

/** La ressource n'existe pas ou n'est pas accessible */
class NotFoundException(message: String) : RuntimeException(message)

/** La ressource existe déjà */
class AlreadyExistsException(message: String) : RuntimeException(message)
