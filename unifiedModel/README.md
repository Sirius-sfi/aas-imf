# Unified Model

In this project we attempt to create a model that unifies the asset administration shell with the integrated information framework (and Model Builder).

This folder contains the exact same model in three different file formats. If you want to change the model, you have to make the change in all three files. 

IMF provides semantics that enable mapping between AML, AAS and IMFML in a unified manner

## Quetions
1. What is the boundary between AML and CAEX (IEC 62424)?
   1. AML is built on the CAEX format
   1. Being AML compliant requires using AML libraries in CAEX

## Proposals
1. AspectObjects are AAS SubModels of kind "Instance". 
1. AspectObjects are AML Instances 
1. IMF types are AAS SubModels of kind "Template" with a semantic ID to a IMF RDL
    1. The SubModel can be retrived directly from a IMF endpoint
1. IMF types are AML Roles. This also means that the serialization from AAS to AML has to be IMF aware
1. It is possible to annotate existing AML with IMF semantics, as an alternative to AAS semantics, to convert to AAS and IMFML

## Decisions
1. Do we utilize AAS Views in any way?
1. Should AML be asset or aspect oriented?


## Asset administration shell
To open the `.aasx` file, you must download the AAS package explorer

## Automation ML
To open the `.aml` file, you can download the AML editor

## IMF Markup language
Supported natively in ModelBuilder

