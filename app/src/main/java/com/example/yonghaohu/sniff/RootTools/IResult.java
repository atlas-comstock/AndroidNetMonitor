package com.example.yonghaohu.sniff.RootTools;

import java.io.Serializable;

/**
 * Implement this interface and inject the resulting object
 * when invoking <code>sendShell</code>.
 * <code>RootTools</code> comes with a reference implementation:
 * <code>RootTools.Result</code>
 */
public interface IResult {
    public abstract void process(String line) throws Exception;
    public abstract void onFailure(Exception ex);
    public abstract void onComplete(int diag);

    public IResult      setProcess(Process process);
    public Process      getProcess();
    public IResult      setData(Serializable data);
    public Serializable getData();
    public IResult      setError(int error);
    public int          getError();

}
