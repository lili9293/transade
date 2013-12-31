package deburnat.transade.core.storages

import deburnat.transade.core.admins.CoreAdmin.{_platform, st, _st}

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 1/1/14
 * Time: 12:00 AM
 *
 * This is the storage object wrapper.
 */
protected[core] object Storage {

  /**
   * This method is used to get the storage object corresponding to the given format.
   * @param format The storage/repository format.
   * @return The adequate storage object otherwise null if the format isn't supported.
   */
  def getStorage(format: String): IStorage = try{
    Class.forName( //st =: Storage, _st = storage
      _platform(_st) + format(0).toUpper.toString + format.replaceAll("^.{1}", "").toLowerCase + st
    ).newInstance.asInstanceOf[IStorage]
  }catch{case e: Exception => null} //ClassNotFoundException | InstantiationException | IllegalAccessException e
}
