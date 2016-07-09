package com.bclymer.dailybudget.database

/**
 * Created by Brian on 7/9/2016.
 */
abstract class BaseRepository {


}

class EntityNotFoundException(entity: Class<*>, id: Any) : RuntimeException("Failed to find ${entity.simpleName} with ID=$id")