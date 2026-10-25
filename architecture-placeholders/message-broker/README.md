# Message Broker (proposed)

Shown in the Container diagram (Figure 2) and Deployment diagram (Figure 8).

Durable queue between the IoT Ingestion Service and the Monitoring &
Alert Service. It buffers sensor events so ingestion and processing can
scale separately (ADR-01, ADR-04). Something like RabbitMQ or an MQTT
broker would be used here.

Not implemented in the prototype (the assignment says the event bus
doesn't need to be coded).
