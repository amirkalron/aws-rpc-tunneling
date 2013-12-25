# Aws-rpc-tunneling: use AWS for tunneling your RPC calls


## What is it
common solution to enable RPC call behind firewall\proxy is to use SSH tunneling.
enable RPC tunneling using AWS SQS service.

#### SSH  disadvantages:

- P2P architecture,not suitubale for common cloud multi machine architecture.
- needs some sophisticated configuration
- alsways open channel between nodes

This project uses a different approach - polling via AWS SQS service.
A tunnel object is created on the AWS platform,via SQS service,where producer\consumer push ans pull requests

#### Aadvantages:

- distributes,can be used by multiple slaves architecture
- very easy to configure
- platform indipendent,no need to configure SSH server\firewall settings


 
## How to start

in aws.properties key,set cridentials and proxy settings ( optional)
then,go to RestTunnelTest to view the example
Done!




