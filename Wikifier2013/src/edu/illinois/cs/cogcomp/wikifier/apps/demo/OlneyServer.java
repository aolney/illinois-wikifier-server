package edu.illinois.cs.cogcomp.wikifier.apps.demo;

import java.net.InetSocketAddress;

import java.util.Arrays;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import edu.illinois.cs.cogcomp.wikifier.common.GlobalParameters;
import edu.illinois.cs.cogcomp.wikifier.common.GlobalParameters.SettingManager;
import edu.illinois.cs.cogcomp.wikifier.common.ParameterPresets;
import edu.illinois.cs.cogcomp.wikifier.common.SystemSettings.WikiAccessType;
import edu.illinois.cs.cogcomp.wikifier.inference.InferenceEngine;


/**
 * A simple HTML server
 * @author cheng88
 *
 */
public class OlneyServer{

    @SuppressWarnings("restriction")
    public static void main(String[] args) throws Exception{
        
        System.out.println("Params:"+Arrays.toString(args));
		System.out.println("\t$java OlneyServer <pathToConfigFile> ");

		GlobalParameters.loadConfig(args[args.length-1]);

        InferenceEngine engine = new InferenceEngine(false);
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8800), 0);
        HttpContext wikifyContext = server.createContext("/wikify", new OlneyHandler(engine));
        //wikifyContext.getFilters().add(new ParameterFilter());
        server.start();
    }

}
