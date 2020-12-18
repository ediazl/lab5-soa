package soa.eip;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";
  final String numberOfTweetsDefault = "10"; //Number of tweets to retrieve

  @Override
  public void configure() {
    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .log("Searching twitter for \"${body}\"!")
      .process(bodyPre -> {
        String regex = "max:[0-9]+";
        String body = bodyPre.getIn().getBody(String.class);
        String numberOfTweets = numberOfTweetsDefault;
        //Regular expression that searchs for "max:n"
        String[] query = body.split(" ");
        String queryBody = "";
        for (String word : query) {
          //System.out.println("word:" + word);
          if (word.matches(regex)) {
            // System.out.println("Coincide regexp" + bodyPre.toString());
            numberOfTweets = word.substring(4);
            // System.out.println("numberOfTweets:" + numberOfTweets);
          }
          else {
            queryBody += word + " ";
          }
        }
        bodyPre.getIn().setBody(queryBody);
        bodyPre.getIn().setHeader("numberOfTweets", numberOfTweets);
      })
      .toD("twitter-search:${body}?count=${header.numberOfTweets}")
      .log("Body now contains the response from twitter:\n${body}");
  }
}
