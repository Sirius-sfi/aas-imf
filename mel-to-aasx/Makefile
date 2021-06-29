# $@ is the name of the file to be made.
# $? is the names of the changed dependents. 
# $< the name of the related file that caused the action.
# $* the prefix shared by target and dependent files.

#---------------------------------------------------
# Detect whether Cygwin is used:
# --------------------------------------------------
UNAME_S := $(shell uname -s)
UNAME_S := $(shell echo $(UNAME_S) | tr A-Z a-z)
ifneq (,$(findstring cygwin,$(UNAME_S)))
	IS_CYGWIN := true
endif

# --------------------------------------------------
# Cygwin path workaround, see
# http://blogs.sun.com/kto/entry/adventures_with_mks_and_cygwin 
# each environment variable VAR we want to use here needs
# to get this treatment first:
# VAR:=$(call FullPath,"$(subst \,/,$(VAR))")
# --------------------------------------------------
ifdef IS_CYGWIN
	CYGPATH_CMD=cygpath -a -s -m
	define FullPath
	$(subst //,/,$(shell $(CYGPATH_CMD) "$1"))
	endef
endif


# Download blabel here: https://github.com/aidhog/blabel/ and build it using maven
# I had to change the pom.xml file by setting maven.compiler.target/source to 1.7 instead of 1.6 YMMV
# Export the system variable BLABEL_JAR and point it to the location of the jar after building
ifdef BLABEL_JAR
	blabel = java -jar $(BLABEL_JAR)
else
	blabel = blabel
endif

# --------------------------------------------------
# SPARQL
# --------------------------------------------------

#SET JENA_HOME back to windows mode. Jena needs this for classpath calculation, and since Java "is Windows"
# the classpath must be windows also

ifdef IS_CYGWIN
	JENA_HOME:=$(call FullPath,"$(subst \,/,$(JENA_HOME))")
endif

ifdef IS_CYGWIN
	sparql=$(JENA_HOME)/bat/arq.bat
	rsparql=$(JENA_HOME)/bat/arq_remote.bat
	update=$(JENA_HOME)/bat/update.bat
	turtle=$(JENA_HOME)/bat/turtle.bat
	riot=$(JENA_HOME)/bat/riot.bat
else
	sparql=$(JENA_HOME)/bin/arq
	rsparql=$(JENA_HOME)/bin/rsparql
	update=$(JENA_HOME)/bin/update
	turtle=$(JENA_HOME)/bin/turtle
	riot=$(JENA_HOME)/bin/riot
endif

lutra = lutra
mel2ottr = java -jar bin/MEL2OTTR.jar
python = python3

# --------------------------------------------------
# targets
# --------------------------------------------------

# check-env:
# ifndef JENA_HOME
	# $(error JENA_HOME is not set)
# endif

clean:
	rm -f rdf/mel.ttl

rdf/mel.ttl: csv/MEL_v03.csv bottr/mel.bottr
	$(lutra) \
	--library ottr/tpl/ \
	--libraryFormat stottr \
	--fetchMissing \
	--inputFormat bottr \
	--stdout \
	bottr/mel.bottr \
	> $@


aas/%.aasx: python/rdf-to-aas.py
	$(python) \
	python/rdf-to-aas.py


# csv-vsm: \
	# csv/vsm/AlternativeVDS.csv \
	# csv/vsm/Declarations.csv \
	# csv/vsm/Function.csv \
	# csv/vsm/GenericValveType.csv \
	# csv/vsm/MMD_NPS.csv \
	# csv/vsm/Pressure.csv \
	# csv/vsm/ServiceCode.csv \
	# csv/vsm/ServiceGroup.csv \
	# csv/vsm/Size.csv \
	# csv/vsm/Temperature.csv \
	# csv/vsm/ValveTypeSize.csv \
	# csv/vsm/VDS.csv \
	# csv/vsm/VDS_Superclass.csv \
	# csv/vsm/VSM_Matrix_SLAVE.csv

# csv-jsp2: \
	# csv/jsp2/line-data.csv \
	# csv/jsp2/valve-tags.csv \
	# csv/jsp2/valve-tags-service-group.csv

# ottr-vsm: \
	# ottr/vsm/Declaration.stottr \
	# ottr/vsm/DnMmdMapping.stottr \
	# ottr/vsm/Function.stottr \
	# ottr/vsm/Pressure.stottr \
	# ottr/vsm/Qualifier.stottr \
	# ottr/vsm/ServiceCode.stottr \
	# ottr/vsm/ServiceGroup.stottr \
	# ottr/vsm/ServiceServiceGroup.stottr \
	# ottr/vsm/ServiceServiceGroupList.stottr \
	# ottr/vsm/Size.stottr \
	# ottr/vsm/Temperature.stottr \
	# ottr/vsm/ValveType.stottr \
	# ottr/vsm/ValveTypeSizeExtraLargeLowerLimit.stottr \
	# ottr/vsm/ValveTypeSizeLarge.stottr \
	# ottr/vsm/ValveTypeSizeLargeLowerLimit.stottr \
	# ottr/vsm/ValveTypeSizeMedium.stottr \
	# ottr/vsm/ValveTypeSizeSmall.stottr \
	# ottr/vsm/VDS.stottr \
	# ottr/vsm/VDSAlternative.stottr \
	# ottr/vsm/VDSAlternativeComment.stottr \
	# ottr/vsm/VDSAlternativeList.stottr \
	# ottr/vsm/VDSDowngraded.stottr \
	# ottr/vsm/VDSDowngradedList.stottr \
	# ottr/vsm/VDSUpgraded.stottr \
	# ottr/vsm/VDSUpgradedList.stottr \
	# ottr/rdfs/SubPropertyOf.stottr \
	# ottr/owl/Class.stottr \
	# ottr/owl/AnnotationAxiom.stottr

# ottr-jsp2: \
	# ottr/tpl/Line.stottr \
	# ottr/tpl/ValveTag.stottr \
	# ottr/tpl/ValveTagPids.stottr \
	# ottr/tpl/ValveTagPosition.stottr \
	# ottr/tpl/ValveTagServiceGroup.stottr \
	# ottr/tpl/ValveTagSizeIn.stottr \
	# ottr/tpl/ValveTagSizeMn.stottr \
	# ottr/tpl/ValveTagType.stottr

# csv/jsp2/%.csv: sql/%.sql
	# $(sql2csv) \
	# -c JSP2/tmc@EIS \
	# -q sql/$*.sql \
	# -o $@

# csv/vsm/%.csv: xls/VSM-vocabulary.xlsx
	# $(in2csv) \
	# --sheet "$*" \
	# xls/VSM-vocabulary.xlsx \
	# > $@
	# bin/trimEmptyLines.sh $@

# rdf/jsp2-model.ttl: bottr/jsp2-model.bottr csv-jsp2 ottr-jsp2
	# $(lutra) \
	# --library ottr/tpl/ \
	# --libraryFormat stottr \
	# --fetchMissing \
	# --inputFormat bottr \
	# --stdout \
	# bottr/jsp2-model.bottr \
	# > $@

# rdf/vsm.ttl: bottr/vsm.bottr csv-vsm ottr-vsm
	# $(lutra) \
	# --library ottr/vsm/ \
	# --library ottr/rdfs/ \
	# --library ottr/owl/ \
	# --libraryFormat stottr \
	# --fetchMissing \
	# --inputFormat bottr \
	# --stdout \
	# bottr/vsm.bottr \
	# > $@

#rdf/mmd/%.ttl: sparql/mmd/%.rq
#	$(rsparql) \
#	--results ttl \
#	--query sparql/mmd/$*.rq \
#	--service "http://data.aibel.com/fuseki/prod/query" \
#	> $@

# signature/mmd/%.sig: sparql/mmd/%.rq
	# $(rsparql) \
	# --results csv \
	# --query sparql/mmd/$*.rq \
	# --service "http://data.aibel.com/fuseki/prod/query" \
	# | tail -n +2 - \
	# > $@

# signature/combined.sig: signature/mmd/mmd_nps.sig signature/mmd/mmd_valve_da.sig
	# cat \
	# signature/mmd/mmd_valve_da.sig \
	# signature/mmd/mmd_nps.sig \
	# > $@

# rdf/mmd_extract.ttl: signature/combined.sig rdf/wpc/metaall_prod.ttl
	# $(extract) \
	# --rdf_file rdf/wpc/metaall_prod.ttl \
	# --signature signature/combined.sig \
	# --output $@

# rdf/valve_case_collected.ttl: rdf/mmd_extract.ttl rdf/vsm.ttl rdf/jsp2-model.ttl sparql/union.rq
	# $(sparql) \
	# --graph rdf/mmd_extract.ttl \
	# --graph rdf/vsm.ttl \
	# --graph rdf/jsp2-model.ttl \
	# --query sparql/union.rq \
	# --results ttl \
	# > $@

# rdf/valve_case.ttl: rdf/valve_case_collected.ttl sparql/remove_ont_decl.rq prefixes.ttl
	# $(update) \
	# --update sparql/remove_ont_decl.rq \
	# --data rdf/valve_case_collected.ttl \
    # --dump | \
	# cat - prefixes.ttl | \
	# $(turtle) \
	# --check \
	# --strict \
	# --syntax turtle \
	# --formatted turtle \
	# > $@

# rdfox/rdf/valve_case.ttl: rdf/valve_case.ttl
	# cp rdf/valve_case.ttl $@

# #rdfox/valve_case_inferred_raw.ttl: rdfox/rdf/valve_case.ttl
# rdfox/valve_case_inferred_raw.ttl:
	# cd rdfox && $(rdfox) sandbox . startup.script

# rdfox/valve_case_inferred.ttl: rdfox/valve_case_inferred_raw.ttl
	# $(update) \
	# --update sparql/ontology-redecl.rq \
	# --data rdfox/valve_case_inferred_raw.ttl \
	# --dump | \
	# cat - prefixes.ttl | \
	# $(turtle) \
	# --check \
	# --strict \
	# --syntax turtle \
	# --formatted turtle \
	# > $@

#	$(turtle) \
#	--check \
#	--strict \
#	--syntax turtle \
#	--formatted turtle \
#	rdfox/valve_case_inferred_raw.ttl \
#	> $@


