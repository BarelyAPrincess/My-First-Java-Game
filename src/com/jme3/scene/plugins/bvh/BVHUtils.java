/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package com.jme3.scene.plugins.bvh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * 
 * @author Nehon
 */
public class BVHUtils
{
	
	public static Animation reTarget( Spatial model, BVHAnimData sourceData, Map<String, String> boneMapping, boolean skipFirstKey )
	{
		Skeleton targetSkeleton = model.getControl( AnimControl.class ).getSkeleton();
		int start = skipFirstKey ? 1 : 0;
		
		Animation sourceAnimation = sourceData.getAnimation();
		Skeleton sourceSkeleton = sourceData.getSkeleton();
		Animation resultAniamtion = new Animation( sourceAnimation.getName(), sourceAnimation.getLength() - start * sourceData.getTimePerFrame() );
		targetSkeleton.updateWorldVectors();
		List<BoneTrack> targetTracks = new ArrayList<BoneTrack>();
		float targetHeight = ( (BoundingBox) model.getWorldBound() ).getYExtent();// getSkeletonHeight(targetSkeleton);
		float sourceHeight = getSkeletonHeight( sourceSkeleton );
		
		Vector3f rootPos = new Vector3f();
		Quaternion rootRot = new Quaternion();
		targetSkeleton.reset();
		targetSkeleton.updateWorldVectors();
		targetSkeleton.setBindingPose();
		
		float ratio = targetHeight / sourceHeight;
		
		for ( int i = 0; i < sourceAnimation.getTracks().length; i++ )
		{
			BoneTrack boneTrack = (BoneTrack) sourceAnimation.getTracks()[i];
			Bone sourceBone = sourceSkeleton.getBone( boneTrack.getTargetBoneIndex() );
			Bone targetBone = targetSkeleton.getBone( boneMapping.get( sourceBone.getName() ) );
			
			int targetBoneIndex = targetSkeleton.getBoneIndex( boneMapping.get( sourceBone.getName() ) );
			if ( targetBoneIndex != -1 )
			{
				Vector3f[] tr = new Vector3f[boneTrack.getTranslations().length - start];
				Quaternion rots[] = new Quaternion[boneTrack.getRotations().length - start];
				float[] times = new float[boneTrack.getTimes().length - start];
				
				// check the root bone (assuming one root, must be the case since it's a bvh file)
				// in this case we relocate it at the same position as the target's skeleton root at the begining of the
				// animation.
				if ( sourceBone.getParent() == null )
				{
					if ( boneTrack.getTranslations().length > start )
					{
						rootPos.set( boneTrack.getTranslations()[start] );
					}
					for ( int j = 0; j < times.length; j++ )
					{
						int tInd = j + start;
						times[j] = boneTrack.getTimes()[tInd];
						tr[j] = boneTrack.getTranslations()[tInd].subtract( rootPos ).multLocal( ratio );
						Quaternion quaternion = boneTrack.getRotations()[tInd].mult( rootRot );
						rots[j] = quaternion;
					}
					
				}
				else
				{
					
					Quaternion invRot = sourceBone.getModelSpaceRotation().inverse();
					invRot.multLocal( sourceBone.getModelSpaceRotation() ).inverseLocal();
					
					invRot.normalizeLocal();
					System.out.println( "bone :  " + targetBone.getName() );
					
					for ( int j = 0; j < times.length; j++ )
					{
						int tInd = j + start;
						// times
						times[j] = boneTrack.getTimes()[tInd];
						
						// translations
						tr[j] = boneTrack.getTranslations()[tInd].mult( ratio );
						
						// rotations
						System.out.println( "anim frame :  " + tInd );
						outPutRotation( invRot.mult( boneTrack.getRotations()[tInd] ) );
						
						rots[j] = boneTrack.getRotations()[tInd].inverse();// mult(
																							// targetBone.getParent().getInitialRot().mult(targetBone.getInitialRot()).inverse());
					}
					// targetBone.setBindTransforms(sourceBone.getInitialPos().mult(ratio),sourceBone.getInitialRot().clone());
				}
				
				BoneTrack targetTrack = new BoneTrack( targetBoneIndex, times, tr, rots );
				targetTracks.add( targetTrack );
			}
			
		}
		BoneTrack[] tracks = new BoneTrack[targetTracks.size()];
		targetTracks.toArray( tracks );
		resultAniamtion.setTracks( tracks );
		
		return resultAniamtion;
	}
	
	public static float getSkeletonHeight( Skeleton targetSkeleton )
	{
		float maxy = -100000;
		float miny = +100000;
		
		for ( int i = 0; i < targetSkeleton.getBoneCount(); i++ )
		{
			Bone bone = targetSkeleton.getBone( i );
			if ( bone.getLocalPosition().y > maxy )
			{
				maxy = bone.getLocalPosition().y;
			}
			if ( bone.getLocalPosition().y < miny )
			{
				miny = bone.getLocalPosition().y;
				System.out.println( bone.getName() + " " + miny );
			}
		}
		System.out.println( maxy - miny );
		return maxy - miny;
	}
	
	private static void outPutRotation( Quaternion q )
	{
		
		float[] angles = new float[3];
		q.toAngles( angles );
		
		System.out.println( "rotation x: " + angles[0] * FastMath.RAD_TO_DEG );
		System.out.println( "rotation Y: " + angles[1] * FastMath.RAD_TO_DEG );
		System.out.println( "rotation Z: " + angles[2] * FastMath.RAD_TO_DEG );
		
	}
	
	private static Quaternion invert( Quaternion q )
	{
		
		float[] angles = new float[3];
		q.toAngles( angles );
		
		angles[0] = -angles[0];
		angles[1] = -angles[1];
		angles[2] = -angles[2];
		return new Quaternion().fromAngles( angles );
	}
}
