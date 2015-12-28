package com.example.yonghaohu.sniff.RootTools;

/**
 * Developers may throw this exception from within their code
 * when using IResult as a means to change the program flow.
 */
public class RootToolsException extends Exception {
    public RootToolsException(Throwable th) {
        super(th);
    }
}
