# DO NOT REMOVE THOSE LINE
OUTPUT=./out/
SOURCE=./src/
# DO NOT REMOVE THOSE LINE

# we are calling init, then test_hello_world
all: init $(OUTPUT)test_hello_world

#######################################
# generic rules to compile our files  #
#######################################
$(OUTPUT)%.cmi: $(SOURCE)%.mli
	ocamlc -c $< -o $@ -I $(OUTPUT)

$(OUTPUT)%.cmo: $(SOURCE)%.ml
	ocamlc -c $< -o $@ -I $(OUTPUT)

##############################
# Rules to cmpile our files  #
##############################

# we need hello_world.cmo and test_hello_world.cmo to create test_hello_world
$(OUTPUT)test_hello_world: $(OUTPUT)hello_world.cmo $(OUTPUT)test_hello_world.cmo
	ocamlc $^ -o $@ -I $(OUTPUT)
# we need to compile the interface first to get hello_world.cmo
$(OUTPUT)hello_world.cmo: $(OUTPUT)hello_world.cmi
# we need to compile hello_world.cmo first
$(OUTPUT)test_hello_world.cmo: $(OUTPUT)hello_world.cmo

##########
# Tasks  #
##########

init:
	mkdir -p $(OUTPUT)

clean:
	rm -rf $(OUTPUT)

.PHONY: clean