package edu.illinois.cs.cogcomp.wikifier.apps.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.net.URI;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.wikifier.common.GlobalParameters;
import edu.illinois.cs.cogcomp.wikifier.inference.InferenceEngine;
import edu.illinois.cs.cogcomp.wikifier.models.LinkingProblem;
import edu.illinois.cs.cogcomp.wikifier.models.WikiCandidate;
import edu.illinois.cs.cogcomp.wikifier.models.Mention;
import edu.illinois.cs.cogcomp.wikifier.models.ReferenceInstance;
import edu.illinois.cs.cogcomp.wikifier.models.WikiCandidate;

import com.google.gson.Gson;

class Candidate {
  private double score;
  public double getScore() { return score; }
  public void setScore(double in) { this.score = in;  }
  private String wikiTitle;
  public String getWikiTitle() { return wikiTitle; }
  public void setWikiTitle(String in) { this.wikiTitle = in;  }
  private int wikiId;
  public int getWikiId() { return wikiId; }
  public void setWikiId(int in) { this.wikiId = in;  }
  private String attributes;
  public String getAttributes() { return attributes; }
  public void setAttributes(String in) { this.attributes = in;  }
}
class Entity {
  private String surfaceForm;
  public String getSurfaceForm() { return surfaceForm; }
  public void setSurfaceForm(String in) { this.surfaceForm = in;  }
  private int start;
  public int getStart() { return start; }
  public void setStart(int in) { this.start = in;  }
  private int stop;
  public int getStop() { return stop; }
  public void setStop(int in) { this.stop = in;  }
  private Candidate[] candidates;
  public Candidate[] getCandidates() { return candidates; }
  public void setCandidates(Candidate[] in) { this.candidates = in;  }

}

class Result {
  private String inputText;
  public String getInputText() { return inputText; }
  public void setInputText(String in) { this.inputText = in;  }
  private Entity[] entities;
  public Entity[] getEntities() { return entities; }
  public void setEntities(Entity[] in) { this.entities = in;  }
}

class Request {
  private String text;
  public String getText() { return text; }
  public void setText(String in) { this.text = in;  }
}

@SuppressWarnings("restriction")
public class OlneyHandler implements HttpHandler {

    InferenceEngine inference;
    private static final String warmUp = "Emotional stress can either increase or decrease TRH and TSH secretion, depending upon circumstances.";

    public OlneyHandler(InferenceEngine engine) throws Exception {
        inference = engine;
        annotate(warmUp);
    }

    private String annotate(String text) throws Exception {
        TextAnnotation ta = GlobalParameters.curator.getTextAnnotation(text);
        

        LinkingProblem problem = new LinkingProblem(text, ta, new ArrayList<ReferenceInstance>());
        inference.annotate(problem, null, false, false, 0);

        //HTML formatted string
        //String resp = problem.wikificationString(false);
        System.out.println("HTML output");
        System.out.println( problem.wikificationString(false) );

        //Detailed JSON of Result
        String resp = getWikifierOutput(problem);
        // System.out.println("JSON output");
        // System.out.println(getWikifierOutput(problem));

        return resp;
    }

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {

            //CORS, return immediately
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

            //input parameter
            String text = "";

            //set input parameter according to request type, GET or POST
            if( exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                @SuppressWarnings("unchecked")
                // ripping out the more flexible query string approach because it conflicts with our JSON POST approach
                // Map<String, Object> params = (Map<String, Object>) exchange.getAttribute("parameters");
                // text = "" + params.get("text");
                URI requestedUri = exchange.getRequestURI();
                String query = requestedUri.getRawQuery();
                String param[] = query.split("[=]");
                text = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
            } else {
                Gson gson = new Gson();
                String body = IOUtils.toString(exchange.getRequestBody(), CHARSET );
                Request req = gson.fromJson( body , Request.class);
                text = req.getText();
            }

            //Process if long enough and output
            byte[] responseBytes;
            if (text == null || text.length() < 5) {
                responseBytes = "Text too short!".getBytes();
                exchange.sendResponseHeaders(200, responseBytes.length);
            } else {
                
                responseBytes = annotate(text).getBytes(CHARSET);
                exchange.getResponseHeaders().set("Content-Type", String.format("application/json; charset=%s", CHARSET));
                exchange.sendResponseHeaders(200, responseBytes.length);
            }

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getScoresString(WikiCandidate c) throws Exception{
	    StringBuilder res=new StringBuilder();
		res.append("["+c.getTid()+"("+c.titleName+")"+"/"+c.rankerScore+"] - [");
		double[] features=c.getRankerFeatures();
		res.append(features[0]);
		for(int i=1;i<features.length;i++)
			res.append(","+features[i]);
		res.append(']');
		return res.toString();
	}
	
    public static String getTitleCategories(String title) {
        return StringUtils.join(GlobalParameters.getCategories(title),'\t');
    }
			
	public static String getWikifierOutput(LinkingProblem problem) {
        Result res = new Result();
        List<Entity> entities = new ArrayList<Entity>();
        res.setInputText( problem.text );
		for(Mention entity : problem.components){
			if(entity.topCandidate == null)
				continue;
            Entity myEntity = new Entity();
            myEntity.setSurfaceForm( entity.surfaceForm.replace('\n', ' ') );
            myEntity.setStart(entity.charStart);
            myEntity.setStop(entity.charStart+entity.charLength);

            List<Candidate> candidates = new ArrayList<Candidate>();
			for(int i=0;i<entity.candidates.get(0).size();i++) {
                Candidate candidate = new Candidate();
				WikiCandidate c = entity.candidates.get(0).get(i);
                candidate.setWikiTitle(c.titleName);
                candidate.setWikiId( c.getTid() );
                candidate.setScore( c.rankerScore );
                candidate.setAttributes( getTitleCategories(c.titleName) );
                candidates.add(candidate);
			}
            myEntity.setCandidates( candidates.toArray(new Candidate[0]) );
			entities.add(myEntity);
		}
        res.setEntities( entities.toArray( new Entity[0]) );
        Gson gson = new Gson();
        String json = gson.toJson(res); //convert 
		return json;
	}

}
