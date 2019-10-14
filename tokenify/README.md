<p align="center">
  <a src='https://lab.onesaitplatform.com/web/tokenify/'>
    <img src='https://github.com/onesaitplatform/onesaitplatform-revolution-revolution-team/blob/master/resources/header.PNG'/>
  </a>
</p>
 
## Description 
<p>
Tokenify is a utility aimed at making it easier for Data Scientists to work with datasets that contain sensitive information. <br />
It allows selecting the fields considered sensitive and their subsequent transformation into tokenized values, keeping the rest of the fields unchanged. In this way it is possible to safely use real data, instead of having to resort to synthetic data or non-representative samples.<br />
</p>
<p>
The objective of the proposal presented aims to provide business functionality both to users belonging to the community already familiar with platform capabilities,and to external users in order to broaden the platform user base.<br />
It is not intended that the work has the reach of a mere demonstrator without real utility, nor in technical functionalities that do not add value to the end users.<br />
</p>

## How?
We are sure that we have a unique offer in the market based on a combination of differential elements:<br />
<p align="center">   
	Tokenify provides three tokenization methods:<br />
</p><br />
<p>   	
• FPE, format-preserving encryption, which transforms the values ​​through encryption but preserves the original format of the data so that they maintain the properties that allow verifying the suitability of the algorithms.<br />
• AES, symmetric encryption, which also uses encryption but does not preserve the format. This tokenization technique is safer but less convenient.<br />
• Random map, which uses a trivial obfuscation technique. It is the least safe technique but the fastest in computational terms.<br />
</p>


## How to build the app and deploy it on server?
<p>
  1. This application front is made with Angular material so you need to have Node.js installed in your computer. <br />
  2. Download this repostory. <br />
  3. With terminal go where you have this project(front/TokenizerApp).<br />
  4. npm install <br />
  5. To run localy: ng serve --proxy-config proxy.conf.json <br />
  6. to generate war: ng build  --base-href ./ --prod (generate the folder dist) and then deploy it on server <br />
</p>



