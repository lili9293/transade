package deburnat.transade.core.storages

import deburnat.transade.core.admins.CoreAdmin.{platform, st, _st}


/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 7/27/13
 * Time: 12:10 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[core] object Storage {
  //st =: Storage, _st = storage

  def getStorage(format: String): IStorage = try{
    Class.forName(
      platform(_st, false) + format(0).toUpper.toString + format.replaceAll("^.{1}", "").toLowerCase + st
    ).newInstance.asInstanceOf[IStorage]
  }catch{case e: Exception => null} //ClassNotFoundException | InstantiationException | IllegalAccessException e
}
