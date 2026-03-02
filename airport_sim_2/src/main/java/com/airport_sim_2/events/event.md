purpose: Delivers information between the Model View and Controller.
So the events recieved in any case may vary wildly.
Maybe events that identify the sender and reciever?
    Helps ensure messages are landing where intended and sent from where they're expected.
What has happened? => 


function


what does an event need to hold?
- intended changes for holding pattern

what happened? 
- aircraft failure

when it happenend: 
- timestamps

where it came from:
    View -> Controller & Controller -> View
    Model -> Controller & Controller -> Model
    that's only 4 main event types
    then what does each event possibly do?

what kind of events do we even want?
runway
aircrafthttps://github.com/ismail-abdul/airport_sim.git
    enter holding pattern

need to know what steps to take under each event.

# ModelEvent
new_aircraft (i.e. enters the holding pattern)
aircraft failure(i.e. internal mechanical failure)
runway closure & opening
runway landing (i.e. aircraft leaves from the holding pattern, and descends on the runway)
runway mode switch(i.e. interchange between landing, take-off and mixed-mode)


