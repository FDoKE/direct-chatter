# Description

Simple implementation of hole punching to produce direct connection between two clients that may be behind NAT using
mediator server.

# HowTo

1. Run `direct-chatter-mediator` on dedicated server with static ip to which both clients can connect
2. Run `direct-chatter-client` on both clients with `mediator IP` as argument
3. If everything is ok clients will be connected between each other directly

### Problems

1. Clients must be behind different NAT device (if you are running both the clients under same NAT device then it may
   not work because not all NAT devices support hair pinning i.e, both clients send packets to NAT's external IP which
   needs to be passed to itself)
2. Some NAT's can behave differently, opened external port for `CLIENT1->MEDIATOR` (which mediator is sending
   to `CLIENT2`) will be different for `CLIENT1->CLIENT2` and whole hole punching wont work