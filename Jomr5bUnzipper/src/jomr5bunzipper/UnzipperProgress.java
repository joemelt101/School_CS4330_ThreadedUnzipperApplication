/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jomr5bunzipper;

/**
 *
 * @author Jared
 */

@FunctionalInterface
public interface UnzipperProgress
{
    public void progress(String status, float percentDone, String fileName, Boolean newFile);
}
